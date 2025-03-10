# alt big ears festival app

because the official festival app was no good!  
[kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) targeting ios and android. ui written in [compose multiplatform](https://www.jetbrains.com/compose-multiplatform/). there's a server module in there that doesn't do anything yet. most of the interesting stuff is in `composeApp/commonMain`.

## how to run

- open the project in android studio, build and run `composeApp` normally for android  
- open `iosApp/iosApp.xcodeproj` in xcode to build/deploy the ios app  

## about

this project is the result of [vibe coding](https://arstechnica.com/ai/2025/03/is-vibe-coding-with-ai-gnarly-or-reckless-maybe-some-of-both/) with [claude code](https://docs.anthropic.com/en/docs/agents-and-tools/claude-code/overview) and spending too much money on anthropic api tokens. claude even generated and executed quite a few of the git commits in this repository. singularity? not quite, but the results are pretty impressive.  

after providing a basic project structure, claude generally accomplished everything i asked it to. it struggled setting up the [sqldelight](https://github.com/sqldelight/sqldelight) database, but once i set up the dependencies and basic database structure, it was off to the races.  

the code definitely isn't great though - i probably wouldn't put logic for loading the content of all three tabs inside a single viewmodel, but hey, whatever works! surrender to our ai overlords! just kidding. don't do that. only do that for weekend projects that no other engineer will ever have to touch.  
