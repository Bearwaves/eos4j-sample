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
provide that. Copy the `SDK/` directory provided by Epic to `desktop/EOS/SDK`.

### Dev Auth

The eos4j sample uses Developer Auth as its login mechanism, using the
[Dev Auth Tool](
https://dev.epicgames.com/docs/epic-account-services/developer-authentication-tool
) provided in the SDK. You should run it on `localhost:1234`,
with the credential name `eos4j`.

### Stats, achievements, etc.

To use the stats, achievements, leaderboards and so on, you'll need to set these
up in your EOS developer portal. The sample is built in such a way that you can
use whatever values for these you like; if you want something quick, Epic
provide a [template](
https://dev.epicgames.com/docs/epic-games-store/services/epic-achievements/achievements-setup#method-2-download-the-epic-games-store-achievements-template
).

## Build and run

The sample app uses Gradle as its build system, and includes a Gradle wrapper
for your convenience. To run the app, simply execute:

```sh
# Linux / macOS
./gradlew desktop:run

# Windows
gradlew.bat desktop:run
```
