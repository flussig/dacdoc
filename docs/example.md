![example.md](../dacdoc-resources/circle-orange-12px.png "checked on 2021-04-05T16&#058;45&#058;43.213&#010;last updated on 2019-09-24T05&#058;53&#058;08&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit 232517638cf9c3b363bb388d1a9a9fc6a9fd0a67")

# What is this page?
(hey, by the way, I encourage you to hover over those colored circles - it will give you insight on who was the last user to change this given piece of documentation!)

This is an example of testable documentation. Your documentation can contain some testable statements such as external links or step-by-step instructions. They can be tested by **dacdoc-maven-plugin**.

Let's show this and start with something simple. 
This is link to this ![4d7ba9ef-7dae-4621-a6f6-19854e84fd2b](../dacdoc-resources/circle-green-12px.png "checked on 2021-04-05T16&#058;45&#058;43.190&#010;last updated on 2019-09-24T05&#058;12&#058;20&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit e73428b6d99b186fe5711deaa9fb1308edef4cfa") [page](./example.md). 
It must appear with green indicator (reachable). 
Here's another link - ![0cb7cd15-a8e7-4903-a70f-dcd01398d108](../dacdoc-resources/circle-green-12px.png "checked on 2021-04-05T16&#058;45&#058;43.058&#010;last updated on 2019-09-24T05&#058;23&#058;13&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit fdec2cce4aa65a16a97ee5a76e6cc13ffc88bb89") [google.com](https://www.google.com) - that should also appear green (unless google.com is not reachable at the time of check). 
And this link - ![965f9ce0-cba2-4ac1-8893-8a85b4ecd8f0](../dacdoc-resources/circle-red-12px.png "checked on 2021-04-05T16&#058;45&#058;42.961&#010;last updated on 2019-09-24T05&#058;12&#058;20&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit e73428b6d99b186fe5711deaa9fb1308edef4cfa&#010;gggooogggllleee.com") [gggooogggllleee.com](http://gggooogggllleee.com) - points to non-existing resource and should show as red.

Now let's try something more complex. Imagine you list names of your employees and want to make sure that all the names contain only alphanumeric characters. DacDoc allows you creating **custom tests** for this (written in Java).
Here it is - applied to the following list:

![f182330a-ad80-4bb9-a58f-67b4f6634107](../dacdoc-resources/circle-green-12px.png "checked on 2021-04-05T16&#058;45&#058;42.967&#010;last updated on 2019-09-24T05&#058;23&#058;13&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit fdec2cce4aa65a16a97ee5a76e6cc13ffc88bb89") 
* Jack
* Joe
* Simon


Here's the same check applied to the list with invalid name:

![2c339151-01a6-49ab-b5a4-852744405937](../dacdoc-resources/circle-red-12px.png "checked on 2021-04-05T16&#058;45&#058;42.969&#010;last updated on 2019-09-24T05&#058;53&#058;08&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit 232517638cf9c3b363bb388d1a9a9fc6a9fd0a67") 
* Jack
* Joe
* Simon
* $triker


Here's another example of advanced tests: **compound checks**. 
For example, here's the compound check of a table with links. 
It must show as yellow since some of the links are valid and some are not.

![0773de06-3d3f-4e52-83e0-eb4ece26eb64](../dacdoc-resources/circle-orange-12px.png "checked on 2021-04-05T16&#058;45&#058;43.191&#010;last updated on 2019-09-24T05&#058;12&#058;20&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit e73428b6d99b186fe5711deaa9fb1308edef4cfa")

| description      | link |
| ------------- | :-----|
| google link | ![googleLink](../dacdoc-resources/circle-green-12px.png "checked on 2021-04-05T16&#058;45&#058;42.995&#010;last updated on 2019-09-24T05&#058;12&#058;20&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit e73428b6d99b186fe5711deaa9fb1308edef4cfa") [google](https://www.google.com) |
| shmoogle link |![shmoogleLink](../dacdoc-resources/circle-red-12px.png "checked on 2021-04-05T16&#058;45&#058;43.190&#010;last updated on 2019-09-24T05&#058;23&#058;13&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit fdec2cce4aa65a16a97ee5a76e6cc13ffc88bb89") [shmoogle](http://shmoogle.com) | 
| gggooogggllleee link | ![gggooogggllleeeLink](../dacdoc-resources/circle-red-12px.png "checked on 2021-04-05T16&#058;45&#058;42.968&#010;last updated on 2019-09-24T05&#058;12&#058;20&#010;last modified by Serdar Kurbanov (<serdar.kurbanov@hotmail.com>)&#010;last modified commit e73428b6d99b186fe5711deaa9fb1308edef4cfa&#010;gggooogggllleee.com") [gggooogggllleee.com](http://gggooogggllleee.com) |





