[![Build Status](https://github.com/biospheere/spoticord/workflows/Build/badge.svg?branch=master)](https://github.com/biospheere/spoticord/actions)
[![GitHub contributors](https://img.shields.io/github/contributors/biospheere/spoticord.svg)](https://github.com/Biospheere/spoticord/graphs/contributors/)
[![GitHub Repo stars](https://img.shields.io/github/stars/Biospheere/spoticord?style=social)](https://github.com/Biospheere/spoticord/stargazers)

# spoticord

View your guild's Spotify listening activity.

## ⚡Commands

- `songs` - View the top 10 tracks from the current guild.
- `album` - View the top 10 album from the current guild.
- `artists` - View the top 10 artists from the current guild.
- `time` - See how much time you listened to music on Spotify
- `users` - View the top 10 users in this guild
- `clock` - Shows when you listen to music the most
- `delete` - Delete your stored data from the database
- `history` - View the last 10 tracks you have listened to.

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
DISCORD_PREFIX=+
```

3. Run the Docker App with `docker-compose up -d`
4. That's it! 🎉

## 📷 Screenshots

![Songs](https://i.imgur.com/zS72bjy.png)
![History](https://i.imgur.com/6PDekHm.png)
![Artists](https://i.imgur.com/nuJ0MM8.png)


## ⚖ [License](LICENSE)

MIT © [Niklas](https://github.com/Biospheere/)
