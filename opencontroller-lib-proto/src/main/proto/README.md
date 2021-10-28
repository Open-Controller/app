# OpenController Proto

A Protocol Buffer schema representing an OpenController house.

## [Documentation](https://open-controller.github.io/proto/)

## Usage

This protocol buffer is meant to be used by OpenController servers and clients.  An OpenController server should serve a House message binary, and a client should use a House message.

To generate code from the schema, see [the Protobuf docs](https://developers.google.com/protocol-buffers/docs/reference/overview).

## Examples

- For usage with Rust, see [OpenController/server](https://github.com/Open-Controller/server/blob/master/build.rs).

- For usage with JVM and Gradle, see OpenController/app's [opencontroller-lib-proto](https://github.com/Open-Controller/app/tree/master/opencontroller-lib-proto).
