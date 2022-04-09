# OpenController Proto

A Protocol Buffer schema representing an OpenController house.

## [Documentation](https://open-controller.github.io/proto/)

## Usage

This protocol buffer is meant to be used by OpenController servers and clients.  An OpenController server should serve a House message binary, and a client should use a House message.

To generate code from the schema, see [the Protobuf docs](https://developers.google.com/protocol-buffers/docs/reference/overview).

## Examples

- For usage with Rust, see [OpenController/server](https://github.com/Open-Controller/server/blob/master/build.rs).

- For usage with JVM and Gradle, see OpenController/app's [opencontroller-lib-proto](https://github.com/Open-Controller/app/tree/master/opencontroller-lib-proto).

## License

        Copyright (C) 2022 PJTSearch

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
