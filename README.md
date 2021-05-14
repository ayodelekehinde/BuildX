<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Thanks again! Now go create something AMAZING! :D
***
***
***
*** To avoid retyping too much info. Do a search and replace for the following:
*** github_username, repo_name, twitter_handle, email, project_title, project_description
-->



<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->

[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]



<!-- PROJECT LOGO -->
<br />
<p align="center">
  <a href="https://github.com/Cherrio-LLC/BuildX">
    <img src="https://github.com/Cherrio-LLC/BuildX/blob/master/app/src/main/res/drawable/icon.png" alt="Logo" width="80" height="80">
  </a>

  <h3 align="center">BuildX</h3>

  <p align="center">
    This project was a result of learning and understanding how android apps are being built. Also i wanted to find a way to build android apps on Android Phone. It turned out to be a 
    fun filled experience through thorough research and coding. The app basically lets you create android apps on phone and run it!
    <br />
    <a href="https://github.com/github_username/repo_name"><strong></strong></a>
    <br />
    <br />
    <a href="https://github.com/Cherrio-LLC/BuildX/blob/master/app/release/app-release.apk">View Demo</a>
    ·
    <a href="https://github.com/Cherrio-LLC/BuildX/issues">Report Bug</a>
    ·
    <a href="https://github.com/Cherrio-LLC/BuildX/issues">Request Feature / Ask questions </a>
  </p>
</p>



<!-- TABLE OF CONTENTS -->
<details open="open">
  <summary><h2 style="display: inline-block">Table of Contents</h2></summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgements">Acknowledgements</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

You'll find how you can build android apps on android devices. For advance devs who want to port it into their app; you'll find classes and methods
that will aid your process


### Built With

* [AndroidX](https://developer.android.com/jetpack/androidx)
* [Google Gson](https://github.com/google/gson)
* [Picasso](https://square.github.io/picasso)
* [ECJ](www.eclipse.org/downloads)
* [Jsoup](https://jsoup.org)
* [AAPT Tool](elinux.org/Android_aapt)
* [ZipSigner](https://github.com/kellinwood/zip-signer)




<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these simple steps.

### Prerequisites

A strong knowledge of Java and android build processes.
1. ECJ(Compiler)
2. AAPT
3. Zipsigner
4. Dexing
5. Apk building

### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/Cherrio-LLC/BuildX.git
   ```



<!-- USAGE EXAMPLES -->
## Usage

**For Advance users:** 
check out this class in the project  [BuildTask](https://github.com/Cherrio-LLC/BuildX/blob/master/app/src/main/java/skyestudios/buildx/builderx/BuildTask.java) wich is responsible for
progressively building the app. For example this is the entry point:

```
  private void runAapt() throws Exception {
  // Runs the aapt tool, which its meant for compiling the resources in the project
  ....
  }
```
```
  private void runCompiler(File androidJar, File classesDir) throws Exception {
  // This function compiles the .java classes to .class; readable for the JVM,
  // Takes in the filePath of the android Jar and the filePath where the Java files are located
  ....
  }
```
```
  private void dexLibs() throws Exception
  // This function dexes the libs or dependencies of the projects
  ....
  }
```
```
  private void dexClasses() throws Exception {
  // This function dexes the compiled .class files
  ....
  }
```
```
  private void dexMerge() throws Exception {
  // This function merges the previous dexes, this function can eatup memory alot, so its better run on phones with 2+ ram
  ....
  }
```
```
  private void buildApk() throws Exception {
  // This function builds the apk, as its name implies
  ...
  }
  private void zipSign() throws Exception {
  // finally this signs it with a key
  ...
  }
```
Check out the class for more



<!-- ROADMAP -->
## Roadmap

I stopped developing the app after the first release due to other demands, but i might continue to support the project, but its open-source now, feel free
to contribute!



<!-- CONTRIBUTING -->
## Contributing

Contributions are what make the open source community such an amazing place to be learn, inspire, and create. Any contributions you make are **greatly appreciated**.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request



<!-- LICENSE -->
## License

Distributed under the MIT License. See [License](https://github.com/Cherrio-LLC/BuildX/blob/master/LICENSE.md) for more information.



<!-- CONTACT -->
## Contact

Ayodele Kehinde 
Email: cherrio.llc@gmail.com

or catch me on linkedin

Project Link: [BuildX](https://github.com/Cherrio-LLC/BuildX)



<!-- ACKNOWLEDGEMENTS -->
## Acknowledgements

* []()
* []()
* []()





<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo.svg?style=for-the-badge
[stars-url]: https://github.com/github_username/repo/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo.svg?style=for-the-badge
[license-url]: https://github.com/Cherrio-LLC/BuildX/blob/master/LICENSE.md
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/ayodele-kehinde-958578210
