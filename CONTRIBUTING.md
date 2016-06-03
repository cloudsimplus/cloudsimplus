# How to contribute

Contributions are always welcome and they increase the simulator evolving speed. However, in order to maintain a high code quality and avoid [software rot](https://en.wikipedia.org/wiki/Software_rot), there are a few guidelines that we need contributors to follow so that we can have a chance of keeping on
top of things.

## Getting Started

* Make sure you have a [GitHub account](https://github.com/signup/free)
* Check if you are using the latest development version of CloudSim++
* Submit a [ticket for your issue](issues), assuming one does not already exist. 
* When submitting a ticket for the issue, read carefully the information at the issue submission form.
* Fork the repository on GitHub

## Before Making Changes

CloudSim++ started as a CloudSim fork aiming to fix several software engineering issues that have been found at the original source code and that make CloudSim++ to follow its own path in order to ensure the use of software engineering standards, recommendations and best practices.
Accordingly, if you are willing to perform changes or include some new feature and get these contributions merged in the CloudSim++ code, you have to:

- Avoid completely code duplication. A single line of code that is called twice, requires you to create a method to allow [code reuse](https://en.wikipedia.org/wiki/Code_reuse). Some recomendations are:
    - Methods should be very short, usually no more that 30 lines. Big methods are confusing and difficult to maintain. Usually they just contribute to code duplication.
    - Most of the times, each method has to make just a sigle thing. For instance, if a given method computes the number of million instructions (MI) that a Cloudlet has to executed across all its Processor Elements (PEs) and compute the time that the Cloudlet is expected to finish, you can create a method for each one of this computations.
    - Identifier names have to be strongly meaningful. Avoid abbreviated names, unless the abbreviation is already part of CloudSim++ vocabulary (such as VM, PM, PE, etc).
    - If you have to write a comment to explain a portion of the code, probably you may use the [Extract Method refactor](http://refactoring.com/catalog/extractMethod.html) using your IDE to create a new method to that portion of code. Commonly, you may be able to reduce your comment making it the method name.
- You are strongly recommended to follow [Clean Coding practices](http://cleancoder.com/books).   
- Along the development, you have to check if there is any [Design Pattern](https://en.wikipedia.org/wiki/Software_design_pattern) that can be applied to the solution you will be working on. If your find out further that a given Design Pattern can be used, refactor your code to apply the pattern.
- You have to provide clear and relevant javadoc documentation for your classes, methods, parameters and attributes. Documentation such as "The id", "The name attribute" doesn't help in anyway and just pollute the code. 
- You have to focus on the [SOLID principles](https://en.wikipedia.org/wiki/SOLID_%28object-oriented_design%29) and recomendations such as [DRY](https://en.wikipedia.org/wiki/Don't_repeat_yourself) and [KISS](https://en.wikipedia.org/wiki/KISS_principle). 
- Code formatting also matters. We care about code organization and identation. An untidy code is difficult to read and lack of formmating standards makes the code ugly. By this way, the project includes a [.editorconfig](.editorconfig) file that can be read by your IDE to auto format the code, by means of [EditorConfig project](http://editorconfig.org). You are not required to use it, but at least, use the auto format option of your IDE to tidy the code after you finish working.  

## Making Changes

* Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Only target release branches if you are certain your fix must be on that
    branch.
  * To quickly create a topic branch based on master: `git checkout -b fix/master/my_contribution master`. Please avoid working directly on the `master` branch.
* Check for unnecessary whitespace with `git diff --check` before committing.
* Spend a little time to write good, informative and structured commit messages, describind clearly what you have done. Take the template below:

````
    Commit title that summarizes the work you have done.

    Explain clearly the changes performed, using clear and formal english.
    Try to use list of items to describe each relevant change, as below:
    - Changed ABC.
    - Added XYZ.
    - Removed 123.
````

* Make sure you have added the necessary unit tests for your changes. 
* Run _all_ the tests to assure nothing else was accidentally broken.
* Changes that don't have a set of unit tests or that broke existing tests will not be accepted.

## Submitting Changes

* [Squash all your local commits together](https://robots.thoughtbot.com/git-interactive-rebase-squash-amend-rewriting-history#squash-commits-together) before submitting a pull request. If we have the need to revert your changes, it will be easiear to revert from a single commit.
* [Rebase your changes on top of CloudSim++ master branch at the oficial repository] (https://robots.thoughtbot.com/git-interactive-rebase-squash-amend-rewriting-history#rebase-on-top-of-master) so that you wil get the most recent development version and perform any merge conflicts that may happen.
* [Commit the final changes including something such as "Closes #NUMBER_OF_THE_ISSUE" or "Fixes #NUMBER_OF_THE_ISSUE"] (https://help.github.com/articles/closing-issues-via-commit-messages/) in order to reference the issue in which you were working on.
* Push your changes to a topic branch in your fork of the repository.
* Submit a pull request to the official CloudSim++ repository.
* Wait for your contribution to be assessed.

# Credits

This Contribution Guide was based on [Puppet guide](https://github.com/puppetlabs/puppet/blob/master/CONTRIBUTING.md), recommend by the [Friction project](https://github.com/rafalchmiel/friction). The current guide was adapted and improved to follow CloudSim++ required standards.