# alt big ears festival app

because the official festival app was no good!  
[kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) targeting ios and android. ui written in [compose multiplatform](https://www.jetbrains.com/compose-multiplatform/). there's a server module in there that doesn't do anything yet. most of the interesting stuff is in `composeApp/commonMain`.  

i scraped the big ears website with some janky python scripts that generated [schedule_final.json](https://github.com/swbain/alt-ears/blob/main/composeApp/src/commonMain/composeResources/files/schedule_final.json). that json is parsed on initial app launch and added to a [sqldelight](https://github.com/sqldelight/sqldelight) database, which is then used to manage and cache the user's custom schedule.  

## how to run

- open the project in android studio, build and run `composeApp` for android  
- open `iosApp/iosApp.xcodeproj` in xcode to build/deploy the ios app  

## about

this project is the result of [vibe coding](https://arstechnica.com/ai/2025/03/is-vibe-coding-with-ai-gnarly-or-reckless-maybe-some-of-both/) with [claude code](https://docs.anthropic.com/en/docs/agents-and-tools/claude-code/overview) and spending too much money on anthropic api tokens. claude even generated and executed quite a few of the git commits in this repository. singularity? not quite, but the results are pretty impressive.  

after providing a basic project structure, claude generally handled everything i asked it to. it struggled with setting up the sqldelight database, but once i got the dependencies and basic structure in place, it was off to the races.  

the code definitely isn't great though - i probably wouldn't put logic for loading the content of all three tabs inside a single viewmodel, but hey, whatever works! surrender to our ai overlords! just kidding. don't do that. only do that for weekend projects that no other engineer will ever have to touch.  
