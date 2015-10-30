# AmiiManage

Manage your amiibo on Android. Enhance your experience ! Take what Nintendo said Amiibo would be : a complete experience on ALL your compatible games

## Installation

The entire repo is here, simply clone this repo to have the complete source code

Requirements :
 + Android Studio (using the 1.5)
 + The Android SDK Installed

```
compileSdkVersion 23
buildToolsVersion "23.0.1"
```

```
minSdkVersion 14
targetSdkVersion 23
```

## Usage

No apk are currently are available. The current app state does not permit this. But it is already possible to give a glance into the logic in our beloved amiibos

## Useful classes

### AmiiboHelper

Provides some method useful to populate the IO operations from simple dump to pages

### AmiiboIO

This class helps performing
 + the authentication
 + read
 + write operations

### AmiiboMethods

 + gets the authentication key from its NFC UUID
 + gets the Amiibo identifier (what distincts a bowser from a samus ;) )

## Tests

It currently lacks of tests. The internal structure of the code will be soon updated to let test implementation easier to make

## License

Released under the GNU General Public License v2.0 License. See the [LICENSE](http://www.gnu.org/licenses/gpl-2.0.txt) file for further details.

## Contributing

 1. Fork this repo ! Yeah, No kidding... DO IT
 2. Create a branch for your feature
 3. Send a Pull Request
 4. Share your ideas
