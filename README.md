# Changelog Library for Android

Table of contents

* [Introduction blabla](#introduction-blabla)
  + [What ?](#what)
  + [Why ?](#why)
  + [Overview](#overview)
  + [Features](#features)
  + [Samples](#samples)
* [Getting started](#getting-started)
  + [Include the library](#include-the-library)
  + [Configure the dialog style](#configure-the-dialog-style)
  + [Write the changelog](#write-the-changelog)
  + [Display the dialog](#display-the-dialog)
* [Customisation](#customisation)
  + [Change the dialog title](#change-the-dialog-title)
  + [Choose what release to show](#choose-what-release-to-show)
  + [Define your own styles](#define-your-own-styles)
  + [Acknowledgments](#acknowledgments)

## Introduction blabla 

###  What

A simple and lightweight library written in Kotlin to show ?beautiful? _changelog_ and _what's new_ dialogs.

The changelog information is written in a simple xml file under `res/xml`.

### Why

I wanted a simple dialog to show my users after an app update. So as usual, I began browsing Github and other platforms for a nice library. I found some, for example [https://github.com/MartinvanZ/Inscription](https://github.com/MartinvanZ/Inscription).

The problem ? They all use __WebViews__, meaning that if you want your dialog to match your application theme, you would need to mimic it using CSS (and maintain it in case you change your mind on the colours or other stuffs, which I constantly do). I find CSS quite boring, so I decided to code my own library using only pure Android Views.

### Overview


![overview](https://docs.google.com/drawings/d/e/2PACX-1vQ5SGULmPAaOKT0z2KndudYsIg5JZMZYAeLH4ngAo2rUW6jhFxzd6TeNLNVxLJ3mfDURCdzE2ofsd1-/pub?h=1024)

### Features

* Changelog written in a simple xml format
* Choose which release(s) notes to show
* Lightweight and easy to use
* Flexible title and behavior
* Uses the AppCompat theme by default (primary and accent colors)
* Completely customizable using styles

### Samples

A sample app is available under `samples`. Feel free to clone and test it.

## Getting started

### Include the library

Add the maven repository in your project's `build.gradle`:

```
repositories {
    maven {
        url  "https://dl.bintray.com/derlin/maven" 
    }
}
```

Add the dependency in your module's `build.gradle`:
```
dependencies {
    ...
    implementation 'ch.derlin.changelog:library:1.0@aar'
}
```
### Configure the dialog style

___! Important__ Don't skip this step, or you will run into exceptions at runtime._

In your `styles.xml`, configure the changelog style by setting the `changelogStyle` attribute in your `AppTheme`. A default style is available, called `LibChangelog`:

```xml
<!-- res/values/in styles.xml -->

<!-- Your base application theme. -->
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <!-- ... other configurations ... --> 
    
    <!-- Apply style for changelog -->
    <item name="changelogStyle">@style/LibChangelog</item>
</style>
```


### Write the changelog

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

### Display the dialog

To create and show the dialog from an activity, just call `Changelog.createDialog`. Don't forget to call `show` !

```kotlin
Changelog.createDialog(this).show()
```

__listener__: if you want to get notified when the dialog is dismissed, add an `onDismissListener` to the dialog before calling `shhow`. 

__versions__: in case you want to show only the latest version's changelog, you can use the extension method `getAppVersion`, which returns a `Pair<versionCode, versionName>`.

```kotlin
import ch.derlin.changelog.Changelog
import ch.derlin.changelog.Changelog.getAppVersion

// ...

val version = getAppVersion()
val dialog = Changelog.createDialog(this, versionCode = version.first)
dialog.setOnDismissListener({_ -> doStuffOnDismiss() })
dialog.show()
```

## Customisation

### Change the dialog title

By default, the dialog title is `@string/changelog_title`. You can either override it in `res/values/strings.xml` or pass a string to the `createDialog` method.

### Choose what release to show

By default, the dialog will show the whole changelog. You can specify the `versionCode` argument in order to display only releases with a `versionCode` __equal or greater__.

As stated above, you can use the `getAppVersion` extension method to get the current `versionCode`.

### Define your own styles 

The dialog is made of three parts:

- the dialog view itself (`LinearLayout` with a title and a `RecyclerView`)
- the header entries: contains a title, a date and a summary
- the cell entries: contains a bullet and a text

Everything is completely customizable using only styles !

To begin customizing the dialog, create a `style` inheriting from `LibChangelog` and set the `changelogStyle` attribute to refer to it instead.

In you custom `changelogStyle`, you can modify any style reference (see `res/values/attrs.xml` for a complete list). __Important__: when you override a style, don't forget to specify the parent (`LibChangelog.STYLE`).

An example is worth a thousand words. To make the screenshot on the right in the overview section, here is the code:

```xml
<style name="AppTheme" parent="Theme.AppCompat.Light.DarkActionBar">
    <!-- Apply style for changelog. 
     Just ensure that the style inherits from LibChangelog -->
    <item name="changelogStyle">@style/MyChangelog</item>
</style>

<style name="MyChangelog" parent="LibChangelog">
    <!-- here is the magic -->
    <!-- just override the title and bullet styles -->
    <item name="changelogTitleStyle">@style/MyChangelogHeader</item>
    <item name="changelogBulletStyle">@style/MyChangelogBullet</item>
</style>

<style name="MyChangelogHeader" parent="LibChangelog.HeaderTitle">
    <!-- override the changelog title: change the color and specify a background image -->
    <item name="android:background">@mipmap/parallax_changelog</item>
    <item name="android:textColor">?android:windowBackground</item>
    <item name="android:gravity">bottom</item>
    <item name="android:paddingLeft">10dp</item>
    <item name="android:paddingBottom">20dp</item>
</style>

<style name="MyChangelogBullet" parent="LibChangelog.CellBullet">
    <!-- change the bullets to use some other drawable resource
     add margin to make it better looking -->
    <item name="android:background">@drawable/logo</item>
    <item name="android:layout_marginEnd">10dp</item>
</style>
```

Done !

### Acknowledgments

This library has been motivated by the following project (from whom I reused the xml structure):

* [Inscription](https://github.com/MartinvanZ/Inscription/) by [Martin van Zuilekom](https://github.com/MartinvanZ/)


See here
You? Please create pull requests against the dev branch

© Copyright Lucy Linder


