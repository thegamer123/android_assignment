# Envision Android Assignment

## Introduction


Envision Library is a feature within the Envision app that users a lot after they've scanned a document. It offers them the convenience of storing the scanned documents within the app in an accessible way. This task will be a simple re-implementation of that feature.

## Design


You'll find the design for this feature through this figma link:  https://www.figma.com/file/mc8Ahvua2SU1sSpFLdchOp/%F0%9F%94%B5-Assignments?node-id=1%3A1011

Instructions on how to approach the UX of the Assignment is also provided on the figma page itself. 

## Technical Requirements

1. For the OCR setup please use this POST endpoint: https://letsenvision.app/api/test/readDocument. 

Example usage of the API endpoint: 

`curl --location --request POST 'https://letsenvision.app/api/test/readDocument' \
--header 'Cookie: __cfduid=d97604b6c67574ccd048c013ffbee703a1614774197' \
--form 'photo=@/Users/johndoe/Desktop/Screenshot 2021-02-25 at 23.50.47.png'`

For more details on the API, here's the Postman [link](https://www.getpostman.com/collections/771c175ea7a0e2db34b9). 


2. Make sure that the app is entirely accessible using Talkback. If you're not familar with Android Accessibility, this is a good place to start: https://developer.android.com/codelabs/starting-android-accessibility

3. CameraX should be used for the camera implementation. 

4. Library files will only have to be saved locally for the time being. 

5. Please do the assignment to do in Kotlin.

## Submission

This repo should be forked and submitted with the Assignment. The assignment itself has a deadline of 1 week, if you'd like more time then please let your contact at Envision know about it.

Incase of any questions with regards to the assignment, please write to kk@letsenvision.com 



