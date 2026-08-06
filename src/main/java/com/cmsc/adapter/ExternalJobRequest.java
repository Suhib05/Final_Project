package com.cmsc.adapter;

import java.time.LocalDateTime;

/**
 * A fabrication job request as it arrives from an external client.
 *
 * This is the Adaptee of the Adapter pattern. External clients are not
 * registered members, so their submissions carry a company name and a block of
 * free text rather than a member identifier and structured fields. The class is
 * deliberately left in its own shape and is never modified to suit the rest of
 * the system; {@link ExternalJobAdapter} performs the translation instead.
 *
 * @author Annour Mohamed
 */
public class ExternalJobRequest {

    private String companyName;
    private String contactEmail;
    private String jobDetails;
    private LocalDateTime receivedAt;

    /**
     * Creates an empty external request.
     */
    public ExternalJobRequest() {
    }

    /**
     * Creates an external request as submitted through the public web form.
     * The time of receipt is stamped on arrival.
     *
     * @param companyName name of the external client
     * @param contactEmail address used to reach the client
     * @param jobDetails free-text description of the work wanted
     */
    public ExternalJobRequest(String companyName, String contactEmail,
            String jobDetails) {

        this.companyName = companyName;
        this.contactEmail = contactEmail;
        this.jobDetails = jobDetails;
        this.receivedAt = LocalDateTime.now();
    }

    /**
     * Returns the name of the external client.
     *
     * @return company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets the name of the external client.
     *
     * @param companyName company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Returns the address used to reach the client.
     *
     * @return contact email address
     */
    public String getContactEmail() {
        return contactEmail;
    }

    /**
     * Sets the address used to reach the client.
     *
     * @param contactEmail contact email address
     */
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    /**
     * Returns the free-text description of the work wanted.
     *
     * @return job details
     */
    public String getJobDetails() {
        return jobDetails;
    }

    /**
     * Sets the free-text description of the work wanted.
     *
     * @param jobDetails job details
     */
    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    /**
     * Returns the moment the request arrived.
     *
     * @return time of receipt
     */
    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Sets the moment the request arrived.
     *
     * @param receivedAt time of receipt
     */
    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
