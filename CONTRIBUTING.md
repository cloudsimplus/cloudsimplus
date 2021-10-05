# Contribution Guide

Contributions are always welcome and they speed up the simulator evolution. You can contribute in different ways that are going to be described below.
However, to maintain a high project quality, there are a few guidelines we need contributors to follow. This way we can have a chance of keeping on top of things.
These guidelines are mainly relevant when you are planning to contribute to the project's source code, so that code quality is preserved and [software rot](https://en.wikipedia.org/wiki/Software_rot) is avoided.

# Ways You Can Contribute

1. Clicking on the "Star" button at the top of the project's GitHub page, giving us more visibility.
1. Using CloudSim Plus at your academic work and citing us in your publications. See the [Publications Section](https://cloudsimplus.org/#publications) for more information.
1. Promoting CloudSim Plus at your social networks and research groups.
1. Participating on the [Google Groups Forum](https://groups.google.com/group/cloudsim-plus) by reporting your experiences using the framework, answering other users' questions or simply participating in the discussions. Just make sure you read the forum policies at its home page before posting there.
1. Reporting issues (check instructions below).
1. Improving project's documentation.
1. Fixing bugs and implementing new features.

The sections below present more information on how to contribute in some of these ways.

# Getting Started

If you want to contribute in some of the 3 last ways presented above, please follow these instructions:

1. Make sure you have a [GitHub account](https://github.com/signup/free)
1. [Check if you are using the latest development version of CloudSim Plus](http://cloudsimplus.readthedocs.io/en/latest/syncing-you-fork-or-clone.html). 
1. Submit a [ticket for your issue](https://github.com/manoelcampos/cloudsimplus/issues), assuming one does not already exist. Check the next sub-section for more details. 

## Requesting a Feature or Reporting an Issue

If you just want to request a feature or report an issue, look if the issue/feature you want to report/request hasn't been reported/requested yet at the [issues page](https://github.com/manoelcampos/cloudsimplus/issues). Try checking the existing issues/features and search using some keywords before creating a new ticket. You can also check the [Google Groups Forum](https://groups.google.com/group/cloudsim-plus) to see if there is any discussion about the subject. If the issue/feature has not been created in the issues page yet, feel free to create a ticket there.

If you are planing to fix an issue or implement a feature, after reporting it, we recommend you to firstly discuss the subject at the [Google Groups Forum](https://groups.google.com/group/cloudsim-plus) and your proposed implementation. This is important to make other users and the CloudSim Plus team to know what you are planning to do and to avoid simultaneous work on the same issues/features.

Finally, when submitting a ticket, please read carefully the information at the submission form and make sure each issue is related to a single bug/feature.

## Fixing a Bug or Implementing a New Feature

Before fixing a bug of implementing a new feature, you have to first fork the repository on GitHub. You can fix a bug or implement a feature already reported by other user or by yourself at the issues page, as mentioned in the previous section. 

### 1. Create a Topic Branch for You to Work on

Create a topic branch from where you want to base your work.
  * This is usually the master branch.
  * Only target specific release branches (such as a branch named "cloudsim-plus-1.0.1") if you are certain that your fix must be on that branch.
  * To quickly create a topic branch based on master: `git checkout master -b my_contribution`. 
    Please avoid working directly on the `master` branch.

### 2. Follow These Code Quality Guidelines

The last step before you start coding is to follow the guidelines below, so that your contribution is likely to be merged in the CloudSim Plus code:

- Completely avoid code duplication. A single line of code that is called twice, requires you to create a method to allow [code reuse](https://en.wikipedia.org/wiki/Code_reuse). 
  Some recommendations are:
    - Methods should be very short, usually no more than 20 lines. Big methods are confusing and difficult to maintain. 
      Usually they just contribute to code duplication.
    - Most of the times, each method has to make just a single thing. For instance, if a given method computes the number of 
      million instructions (MI) that a Cloudlet has to execute across all its Processor Elements (PEs) and also compute the time that the 
      Cloudlet is expected to finish, you must create a method for each one of these computations.
    - Identifier names have to be strongly meaningful. Avoid abbreviations, unless the abbreviation is already part of CloudSim Plus 
      vocabulary (such as VM, PM, PE, etc).
    - If you have to write a comment to explain a portion of the code, probably you should use the 
      [Extract Method refactor](http://refactoring.com/catalog/extractMethod.html) in your IDE to create a new method for that portion of code. 
      Commonly, you may be able to reduce and adjust your comment, using it as the method name.
- You are strongly recommended to follow [Clean Coding practices](http://cleancoder.com/books).   
- You are encouraged to design your solution before start programming. Along the entire development cycle, you have to check if there is 
  any [Design Pattern](https://en.wikipedia.org/wiki/Software_design_pattern) that can be applied to the solution you are working on. 
  If you find out further that a given Design Pattern can be used, refactor your code to apply the pattern.
- You have to provide clear and relevant javadoc documentation for your classes, methods, parameters and attributes. 
  Documentation such as "The id" or "The name attribute" doesn't help in anyway and just pollutes the code. 
- You have to focus on application of [SOLID principles](https://en.wikipedia.org/wiki/SOLID_%28object-oriented_design%29) and recommendations such 
  as [DRY](https://en.wikipedia.org/wiki/Don't_repeat_yourself) and [KISS](https://en.wikipedia.org/wiki/KISS_principle). 
- Code formatting also matters, really. We care about code organization and indentation to be applied uniformly throughout the code. 
  An untidy code is difficult to read and the lack of formating standards makes the code ugly. 
  The project includes a [.editorconfig](.editorconfig) file that can be read by your IDE to auto format the code, 
  by means of [EditorConfig IDE Plugins](http://editorconfig.org). 
  You are not required to use it, but at least, use the "auto format" option of your IDE to tidy the code after you finish working.  

### 3. Commit Your Changes

* Check for unnecessary whitespace with `git diff --check` before committing.
* Spend a little time to write good, informative and structured commit messages, describing clearly what you have done. Take the template below as an example:

````
    Commit title that summarizes the work you have done.

    Explain clearly the changes performed, using formal and well-written English.
    Try to use list of items to describe each relevant change, as below:
    - Changed ABC.
    - Added XYZ.
    - Removed 123.
````

* Make sure you have added the necessary unit tests for your changes. 
* Run _all_ the tests to assure nothing else was accidentally broken.
* Changes that don't have a set of unit tests or that break existing tests **will not** be accepted.

### 4. Submit Your Changes

* [Rebase your changes on top of CloudSim Plus master branch at the official repository](https://robots.thoughtbot.com/git-interactive-rebase-squash-amend-rewriting-history#rebase-on-top-of-master) so that you will get the most recent development version and perform any merge conflicts that may happen.
* [Commit the final changes including something such as "Closes #NUMBER_OF_THE_ISSUE" or "Fixes #NUMBER_OF_THE_ISSUE"](https://help.github.com/articles/closing-issues-via-commit-messages/) in order to reference the issue in which you were working on.
* Push your changes to a topic branch in your fork of the repository.
* Submit a pull request to the official CloudSim Plus repository.
* Wait for your contribution to be assessed and thanks in advance for contributing.

# Credits

This Contribution Guide was based on [Puppet guide](https://github.com/puppetlabs/puppet/blob/master/CONTRIBUTING.md), 
recommend by the [Friction tool](https://github.com/rafalchmiel/friction). 
The current guide was adapted and improved to follow CloudSim Plus required standards.