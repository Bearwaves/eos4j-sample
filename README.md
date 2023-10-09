# eos4j Sample

The eos4j sample application is designed to show a basic use case of
[eos4j](https://github.com/Bearwaves/eos4j). It's built using LibGDX for
convenience; this isn't necessary to use eos4j.

All the interesting bits are in [`EpicPlatformManager.java`](
./desktop/src/com/bearwaves/eos4jsample/EpicPlatformManager.java). The rest is
really just application and UI code that isn't particularly relevant to eos4j.

## Prerequisites

You'll need to do a few things before you're able to run the app and start
messing with it.

### EOS setup

You'll need an application set up on the [EOS Portal](
https://dev.epicgames.com/portal/en-US/). You'll need to grab your Product ID,
Sandbox ID, Deployment ID, Client ID and Client Secret. Populate the fields in
[`EpicVars.java`](./desktop/src/com/bearwaves/eos4jsample/EpicVars.java) with
these values.

### SDK setup

For legal reasons eos4j does not include the EOS SDK itself; you'll need to
provide that. Copy the `SDK/` directory provided by Epic into `desktop/EOS/SDK`.

### Dev Auth

The eos4j sample uses Developer Auth as its login mechanism, using the
[Dev Auth Tool](
https://dev.epicgames.com/docs/epic-account-services/developer-authentication-tool
) provided in the SDK. You should run it on `localhost:1234`,
with the credential name `eos4j`.

## Build and run

The sample app uses Gradle as its build system, and includes a Gradle wrapper
for your convenience. To run the app, simply execute:

```sh
./gradlew[.bat] desktop:run
```
