package com.forge.talentacquisitionengine.offerService.offer.integration;

import com.docusign.esign.api.EnvelopesApi;
import com.docusign.esign.client.ApiClient;
import com.docusign.esign.client.auth.OAuth;
import com.docusign.esign.model.*;
import com.forge.talentacquisitionengine.offerService.offer.entity.Offer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.forge.talentacquisitionengine.offerService.offer.integration.DocuSignProperties;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocuSignClient {

    private final DocuSignProperties properties;
    private final OfferDocumentService offerDocumentService;

    public String createEnvelope(Offer offer) {
        try {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(properties.getBasePath());

            byte[] privateKey = Files.readAllBytes(
                    Path.of(properties.getPrivateKeyPath())
            );

            OAuth.OAuthToken token = apiClient.requestJWTUserToken(
                    properties.getIntegrationKey(),
                    properties.getUserId(),
                    List.of("signature", "impersonation"),
                    privateKey,
                    3600L
            );

            apiClient.addDefaultHeader(
                    "Authorization",
                    "Bearer " + token.getAccessToken()
            );

            // Generate offer PDF
            byte[] pdfBytes = offerDocumentService.generateOfferPdf(offer);

            // Build DocuSign document
            Document document = new Document();
            document.setDocumentBase64(
                    Base64.getEncoder().encodeToString(pdfBytes)
            );
            document.setName("Offer Letter - " + offer.getRole());
            document.setFileExtension("pdf");
            document.setDocumentId("1");

            // Candidate as signer
            String candidateName =
                    offer.getApplication().getCandidate().getFirstName()
                            + " "
                            + offer.getApplication().getCandidate().getLastName();

            String candidateEmail =
                    offer.getApplication().getCandidate().getEmail();

            Signer signer = new Signer();
            signer.setEmail(candidateEmail);
            signer.setName(candidateName);
            signer.setRecipientId("1");
            signer.setRoutingOrder("1");

            // Sign here tab anchored to {{SIGN_HERE}} in PDF
            SignHere signHere = new SignHere();
            signHere.setAnchorString("{{SIGN_HERE}}");
            signHere.setAnchorUnits("pixels");
            signHere.setAnchorXOffset("0");
            signHere.setAnchorYOffset("0");

            Tabs tabs = new Tabs();
            tabs.setSignHereTabs(List.of(signHere));
            signer.setTabs(tabs);

            Recipients recipients = new Recipients();
            recipients.setSigners(List.of(signer));

            EnvelopeDefinition envelope = new EnvelopeDefinition();
            envelope.setEmailSubject("Offer Letter — " + offer.getRole());
            envelope.setDocuments(List.of(document));
            envelope.setRecipients(recipients);
            envelope.setStatus("sent");

            EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
            EnvelopeSummary summary = envelopesApi.createEnvelope(
                    properties.getAccountId(),
                    envelope
            );

            return summary.getEnvelopeId();

        } catch (Exception e) {
            throw new RuntimeException(
                    "DocuSign envelope creation failed: " + e.getMessage(), e
            );
        }
    }

    public void cancelEnvelope(String envelopeId) {
        try {
            ApiClient apiClient = new ApiClient();
            apiClient.setBasePath(properties.getBasePath());

            byte[] privateKey = Files.readAllBytes(
                    Path.of(properties.getPrivateKeyPath())
            );

            OAuth.OAuthToken token = apiClient.requestJWTUserToken(
                    properties.getIntegrationKey(),
                    properties.getUserId(),
                    List.of("signature", "impersonation"),
                    privateKey,
                    3600L
            );

            apiClient.addDefaultHeader(
                    "Authorization",
                    "Bearer " + token.getAccessToken()
            );

            Envelope envelope = new Envelope();
            envelope.setStatus("voided");
            envelope.setVoidedReason("Offer cancelled by recruiter");

            EnvelopesApi envelopesApi = new EnvelopesApi(apiClient);
            envelopesApi.update(properties.getAccountId(), envelopeId, envelope);

        } catch (Exception e) {
            throw new RuntimeException(
                    "DocuSign envelope cancellation failed: " + e.getMessage(), e
            );
        }
    }
}