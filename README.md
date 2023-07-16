# Mobile Security Project (raceHW with a virus)

This project is an extension of the RaceHW project from the Mobile Application course. In addition to the original RaceHW functionality, this project introduces a virus app. When the microphone permission is granted, the app will record the user's audio for an entire round of a game and send it to a specified email address.

## Prerequisites

Before running the application, make sure to set the following values in the `local.properties` file:

MAPS_API_KEY=<your_google_maps_api_key><br>
EMAIL_FROM=<fill_sender_email><br>
EMAIL_PASS=<fill_sender_email_password><br>
EMAIL_TO=<fill_destination_email><br>
SMTP_HOST=<fill_smtp_host><br>
SMTP_PORT=<fill_smtp_port><br>

Replace<br> `<your_google_maps_api_key>` with your actual Google Maps API key, <br>
`<fill_sender_email>` with the sender's email address,<br>
`<fill_sender_email_password>` with the application password of the sender's email, <br>
`<fill_destination_email>` with the email address of the destination recipient,<br>
`<fill_smtp_host>` with the SMTP host,<br>
`<fill_smtp_port>` with the SMTP port.

