# Baby

A simple Android-to-Android remote control app using Web Socket protocol.


# CommandProtocol
A JSON object carrys a command body.

Example

```json
{
	"type": <TYPE_OF_COMMAND>,
	"body": {
		<COMMAND_OBJECT>
	}
}
```
- type: Determine body structure
- body: JSON Object of a command. For list of commands see below.

## Commands

## MusicPlayer Command
- Type: MusicPlayer
- body
   ```json
    {
    	"command": 1
    }
    ```
    - command: Control music player playback.
        - 1: Play
        - 2: Stop
        
        
# Credits

- [Java-WebSocket](https://github.com/TooTallNate/Java-WebSocket)
- [Launcher icon](https://www.flaticon.com/free-icon/crying-baby_104985#term=baby%20crying&page=1&position=6) (Icons made by [Freepik](http://www.freepik.com) from [www.flaticon.com](www.flaticon.com) is licensed by [CC 3.0 BY](http://creativecommons.org/licenses/by/3.0/))