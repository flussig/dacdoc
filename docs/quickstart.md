## Quick start
Short instructions to start working with DacDoc:

1. create git repository
2. create empty maven project
3. add dacdoc-maven-plugin to **plugins** section of your **pom.xml**
4. create documentation files in **.md** format in root folder and subfolders of your project
5. surround testable fragments of your documentation with placeholder (remove spaces before and after exclamation signs):
   ```markdown
   ! DACDOC{<your testable documentation fragment>}(<parameters>) !
   ```
   Example of the documentation project can be found [here](./dacdoc-maven-plugin-test). More on the parameters of testable fragment later.
   
6. (optional - if you need custom checks) add dependency on dacdoc-core and create custom check for your documentation fragment
    More on custom checks later.
      
7. create release branch for your documentation and checkout this branch
8. (optional - needed if your project defines custom checks) compile the project with `mvn clean compile`
9. compile documentation from the root of your project using `mvn com.github.flussig:dacdoc-maven-plugin:compile`
10. commit changes to release branch
11. done: now your documentation is ready and has checks applied to testable fragments

## Testable fragments
Testable documentation fragments are placed in curly brackets after DACDOC annotation (see above). Any text can be surrounded by DACDOC annotation. After compilation the annotation is removed and color indicator of check status is placed before the tested text fragment.

To simplify use of DacDoc plugin for users some common checks are present in `dacdoc-core`. For example, check for 2xx response for url. In case a user wants to check availability of a resource (with absolute and relative paths) they can just surround url in standard markdown format with DACDOC annotation without specifying test name and other parameters.
Example (remove spaces before and after exclamation signs):
```markdown
! DACDOC{[my link](./README.md)} !
```

Testable fragment is omitted for complex checks (aggregate of multiple check).

## Parameters
Parameters for DACDOC annotation are given in parenthesis following curly brackets surrounding tested text fragment.
Semicolon `;` is used to separate parameters.

Example (remove spaces before and after exclamation signs):
```markdown
! DACDOC{[my link](./README.md)}(id=mytest123;test=dacdoc-url) !
```
Example of DACDOC complex check (remove spaces before and after exclamation signs):
```markdown
! DACDOC(ids=mytest123,mytest456) !
```

List of parameters:
* `id` - unique ID for the given DACDOC fragment. Useful when complex checks are defined, so this test can be part of this complex check.
* `test` - name of the check defining what check should be used. If no test value is given, then DacDoc will assume that this is simple url test. Custom check names are defined with `com.github.flussig.dacdoc.check.CheckMetadata` annotation (see below).
* `ids` - list of check ids participating in complex check

## Custom checks
To build custom check one needs to:
* specify class for making a custom check. Class can be specified under standard Maven code path: `src/main/java/...` or `src/test/java/...`. DacDoc is relying on standard Maven conventions of source and target folder locations within your project.
* a class for custom check must extend the base check class: `com.github.flussig.dacdoc.check.Check` or - if this is a check that should run once and cache its result - `com.github.flussig.dacdoc.check.SingleExecutionCheck`
* a class for custom check must specify a constructor accepting `(String argument, File file)`. This constructor should call the constructor of a super class with same signature.
  
  `argument` will be assigned to a protected field `argument` that has a value of tested text fragment.
  
  `file` will be assigned to a protected field `file` that will represent currently tested markdown file.
* a class for custom check must override a method `execute` in case you extend `Check` class OR method `performCheck` extend in case you extend `SingleExecutionCheck`.
* a class for custom check can be added a `com.github.flussig.dacdoc.check.CheckMetadata` annotation where one can specify ID that this test is registered with. 
  In case this annotation is not provided - a class name will be used as test ID.

## Check results
After compilation of the documentation, all DACDOC annotations are removed and check indicators are placed before the tested fragment.
Hovering over the indicator, a user can see the information associated with the test: date and time of execution, git user that committed the last commit for tested fragment.

Indicators:
* red - means that check is unsuccessful (all checks are unsuccessful or don't exist for complex check)
* green - means that check is successful (all checks are successful for complex check)
* orange - can be assigned to complex check if part of checks are successful and part is not.
* grey -  means that no test is associated with given DACDOC fragment, so no test can be performed
