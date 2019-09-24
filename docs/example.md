# What is this page?
This is an example of testable documentation. Your documentation can contain some testable statements such as external links or step-by-step instructions. They can be tested by **dacdoc-maven-plugin**.

Let's show this and start with something simple. 
This is link to this !DACDOC{[page](./example.md)}!. 
It must appear with green indicator (reachable). 
Here's another link - !DACDOC{[google.com](www.google.com)}! - that should also appear green (unless google.com is not reachable at the time of check). 
And this link - !DACDOC{[gggooogggllleee.com](http://gggooogggllleee.com)}! - points to non-existing resource and should show as red.

Now let's try something more complex. Imagine you list names of your employees and want to make sure that all the names contain only alphanumeric characters. DacDoc allows you creating custom tests for this (written in Java).
Here it is - applied to the following list:
!DACDOC{
* Jack
* Joe
* Simon
}(id=employeesStartWithA1;test=employeesStartWithACheck)!

Here's the same check applied to the list with invalid name:
!DACDOC{
* Jack
* Joe
* Simon
* $triker
}(id=employeesStartWithA2;test=employeesStartWithACheck)!

Here's another example of advanced tests: compound checks. 
For example, here's the compound check of a table with links. 
It must show as yellow since some of the links are valid and some are not.

!DACDOC(ids=googleLink,shmoogleLink,gggooogggllleeeLink)!

| description      | link |
| ------------- | -----:|
| google link | !DACDOC{[google](https://www.google.com)}(id=googleLink)! |
| shmoogle link |!DACDOC{[shmoogle](https://shmoogle.com)}(id=shmoogleLink)! | 
| gggooogggllleee link | !DACDOC{[gggooogggllleee.com](http://gggooogggllleee.com)}(id=gggooogggllleeeLink)! |





