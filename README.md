# alt big ears festival app

because the official festival app was no good!  
[kotlin multiplatform](https://kotlinlang.org/docs/multiplatform.html) targeting ios and android. ui written in [compose multiplatform](https://www.jetbrains.com/compose-multiplatform/). there's a server module in there that doesn't do anything yet. most of the interesting stuff is in `composeApp/commonMain`.

## how to run

- open the project in android studio, build and run `composeApp` normally for android  
- open `iosApp/iosApp.xcodeproj` in xcode to build/deploy the ios app  

## about

this project is the result of [vibe coding](https://arstechnica.com/ai/2025/03/is-vibe-coding-with-ai-gnarly-or-reckless-maybe-some-of-both/) with [claude code](https://docs.anthropic.com/en/docs/agents-and-tools/claude-code/overview) and spending too much money on anthropic api tokens. a fun experiment for a weekend project, but while perfectly functional for a very small feature set, the code is kind of bad!  
