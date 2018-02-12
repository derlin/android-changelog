# Changelog Library for Android

## Introduction blabla 

###  What ?

A simple and lightweight library written in Kotlin to show ?beautiful? _changelog_ and _what's new_ dialogs.

The changelog information is written in a simple xml file under `res/xml`.

### Why ?

I wanted a simple dialog to show my users after an app update. So as usual, I began browsing Github and other platforms for a nice library. I found some, for example [https://github.com/MartinvanZ/Inscription](https://github.com/MartinvanZ/Inscription).

The problem ? They all use __WebViews__, meaning that if you want your dialog to match your application theme, you would need to mimic it using CSS (and maintain it in case you change your mind on the colours or other stuffs, which I constantly do). I find CSS quite boring, so I decided to code my own library using only pure Android Views.

### Features

* Changelog written in a simple xml format
* Choose which release(s) notes to show
* Lightweight and easy to use
* Flexible title and behavior
* Uses the AppCompat theme by default (primary and accent colors)
* Completely customizable using styles

## Getting started

### The changelog

Create an xml file in `res/xml`, for example `changelog.xml`. The format is rather simple, so an example should be worth a thousand words:

```xml
<?xml version="1.0" encoding="utf-8"?>
<changelog>
   <release version="1.2" versioncode="3" summary="I don't remember what this release is about... But I can assure you the update is worth it." date="2018-05-30">
        <change>Fix bugs introduced in version 1.1</change>
        <change>...</change>
    </release>
    <release version="1.1" versioncode="2" date="2018-05-30">
        <change>Fix the leaking of private information to NSA</change>
        <change>Change colors</change>
        <change>Introduce new bugs</change>
    </release>
    <release version="1.0" versioncode="1" summary="Made it !" date="2018-02-18">
        <change>First commit</change>
    </release>
</changelog>
```

Note that:

* the date is in ISO format: `YYYY-MM-dd`
* the `versionName` and `versionCode` are mandatory and should match the versions stated in `build.gradle`
* `date` and `summary` are optional

### Displaying the dialog

TODO