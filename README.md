
# Android System Design Question

The reason I develop this sample was a system design question that I've been asked during an interview.
`If you need to send user's location to a remote server at every minute, how would you develop this feature in android via kotlin?`

To develop a feature that sends a user's location to a remote server every minute in an Android app using Kotlin, you can follow these steps:

 * Request location permission: 
    The first step is to request location permission from the user using the Android LocationManager API. 
    You can ask for the `ACCESS_FINE_LOCATION` or `ACCESS_COARSE_LOCATION` permission depending on the accuracy required by your app.

 * Create a location listener: 
    Once the user grants permission, you can create a location listener that listens for location updates at a regular interval using the LocationManager API.
    Define a location callback using `LocationCallback` and override its `onLocationResult` function that will be called when a location update is received.

 * Retrieve the user's location: 
    When a new location is received, you can retrieve the latitude and longitude of the user's location using the Location object returned by the `onLocationResult` method of the location listener.

 * Send the location to the remote server: 
    After retrieving the user's location, you can use an HTTP client library to send the location data to the remote server. You can create an HTTP POST request with the user's location data as the request body and send it to the server.

 * Schedule periodic location updates: 
    To send the user's location to the server every minute, I used a coroutine which will be run until `Activity` is alive.
    Other options would be use the AlarmManager API to schedule periodic location updates. You can set an alarm that triggers the location listener at regular intervals using the `setRepeating()` method.

