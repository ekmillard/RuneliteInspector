# RuneLite Event Inspector Launcher
Used for logging packets on live OSRS.

## Using the inspector with Jagex Launcher accounts (Windows)
### Force Runelite to output launcher credentials
1. Install latest runelite exe from https://runelite.net/
2. Go to the Windows Start menu > Runelite (configure)
3. In the Client arguments input box add `--insecure-write-credentials`
4. Save

Now the RuneLite client will save your launcher credentials in user.home/.runelite
To unlink your account simply delete the .runelite/credentials.properties file.

### Run runelite once using launcher to save credentials file
5. Run Jagex Launcher and login
6. Click Play button
7. Close Jagex Launcher & RuneLite

### Now use this launcher script to run the event inspector
8. Run this RuneliteInspectorClient.java using java 11
9. Your login credentials should now automatically fill in
