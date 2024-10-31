#!/bin/bash

target_dir="server/target"
client_dir="client/target"
temp_dir="tmp"

mvn clean install

mkdir -p "$temp_dir"

cp "$target_dir/tp2-g5-server-2024.2Q-bin.tar.gz" "$temp_dir/"
cp "$client_dir/tp2-g5-client-2024.2Q-bin.tar.gz" "$temp_dir/"
cd "$temp_dir"

# Server
tar -xzf "tp2-g5-server-2024.2Q-bin.tar.gz"
chmod +x tp2-g5-server-2024.2Q/run-server.sh
sed -i -e 's/\r$//' tp2-g5-server-2024.2Q/*.sh
rm "tp2-g5-server-2024.2Q-bin.tar.gz"

# Client
tar -xzf "tp2-g5-client-2024.2Q-bin.tar.gz"
chmod +x tp2-g5-client-2024.2Q/*Client.sh
sed -i -e 's/\r$//' tp2-g5-client-2024.2Q/*.sh
rm "tp2-g5-client-2024.2Q-bin.tar.gz"