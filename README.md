[![Build Status](https://github.com/biospheere/spoticord/workflows/Build/badge.svg?branch=master)](https://github.com/biospheere/spoticord/actions)
[![GitHub contributors](https://img.shields.io/github/contributors/biospheere/spoticord.svg)](https://github.com/Biospheere/spoticord/graphs/contributors/)
[![c0debase Discord](https://discordapp.com/api/guilds/361448651748540426/embed.png)](https://discord.gg/BDwBeZ3)

# spoticord

View your guild's Spotify listening activity.


## ⚡Commands 

- `top` - View the top 10 tracks from the current guild.
- `random` - Get a random song from the current guild.
- `album` - View the top 10 album from the current guild.
- `artists` - View the top 10 artists from the current guild.
- `time` - See how much time you listened to music on Spotify

## 🔰 Prerequisites

- [Docker](https://docs.docker.com/get-docker/) 
- [Docker Compose](https://docs.docker.com/compose/install/)

## 🛠 Installation

1. Follow the [Docker CE install guide](https://docs.docker.com/install/) and the [Docker Compose install guide](https://docs.docker.com/compose/install/), which illustrates multiple installation options for each OS.
2. Set up your environment variables/secrets in `.env` file
```
MYSQL_ROOT_PASSWORD=???
MYSQL_DATABASE=Tracks
DATABASE_PASSWORD=???
DATABASE_USER=root
DATABASE_HOST=mysql
DISCORD_TOKEN=???
```

3. Run the Docker App with `docker-compose up -d`
4. That's it! 🎉

## 📷 Screenshots

![](https://i.imgur.com/ei1Mnkp.png)

## ⚖ [License](LICENSE)
MIT © [Niklas](https://github.com/Biospheere/)

 