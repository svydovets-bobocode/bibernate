# Contributing to Bibernate project within Svydovets Team

## Important links
* [Google Drive Workspace](https://drive.google.com/drive/u/0/folders/1CUgg4TeZEbQSS-XSHp2LqBJRtXfZLnF_)
* [Trello board](https://trello.com/b/y3BO8jP6/bibernate-project)
* [Figma workspace](https://www.figma.com/team_invite/redeem/2jzj6x6Zz6Q2V0OlcJctWm)
* [Project requirements](https://docs.google.com/document/d/1aTRPRQ5yPc1nVmUtLfdP68Vh9UbPJ9gJR2_uevkg6GI/edit)

## Branching strategy

In this repository we maintain **one key development branch** [dev](https://github.com/svydovets-bobocode/bibernate/tree/dev). 
In most cases it should be updated via the Pull Request.
The ```master``` branch will contain only stable releases.


## Creating a Pull Request


### Branching naming convention 
A branch name... 
* should start from the word "feature" or "bugfix" (according to the type of ticket you are working on)
* then following ticket name from Trello (e.g. "BN-34")
* optionally there may follow short description

Example

* ```feature/BN-5/baseStructure```
* ```bugfix/BN-23```


### Commits
* In case you are working on the Trello ticket, please make sure that you always add its full number to every commit message
* Please provide a descriptive messages
* Use rebase


### Definition of done
You are ready to create a PR for your ticket if the following criteria are met:
* Local ```mvn verify``` is passing without the errors
* Ticket`s acceptance criteria are met
* Your code is covered by tests
* Your code is covered by JavaDocs on the main methods
