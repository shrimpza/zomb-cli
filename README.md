# ZOMB CLI
[![Build Status](https://drone.io/github.com/shrimpza/zomb-cli/status.png)](https://drone.io/github.com/shrimpza/zomb-cli/latest)

CLI Client application for executing [ZOMB](https://github.com/shrimpza/zomb/)
requests from a terminal.

## Usage

`java -jar zomb-cli.jar [options] <plugin-name> <command> [arguments]`

- **[options]**
 - `--config=/path/to/config.file`<br/>
   optionally provide a specific configuration file to use, rather than using
   the default located in `~/.zomb-cli`
- **plugin-name**<br/>
  name of plugin to execute
- **command**<br/>
  command to execute, provided by plugin specified
- **[arguments]**<br/>
  optional arguments required by the command
