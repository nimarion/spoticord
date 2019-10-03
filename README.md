[![Build Status](https://travis-ci.org/Biospheere/spoticord.svg?branch=master)](https://travis-ci.org/Biospheere/spoticord)
[![GitHub contributors](https://img.shields.io/github/contributors/biospheere/spoticord.svg)](https://github.com/Biospheere/spoticord/graphs/contributors/)
[![c0debase Discord](https://discordapp.com/api/guilds/361448651748540426/embed.png)](https://discord.gg/BDwBeZ3)

# spoticord

View your guild's Spotify listening activity.


## :zap: Commands 

- `top` - View the top 10 tracks from the current guild.
- `random` - Get a random song from the current guild.

![](https://i.imgur.com/ei1Mnkp.png)

## 🔰 Prerequisites

- Docker 
- Discord Bot Token 

## 🛠 Installation

```
docker run --name some-mongo -d mongo
docker run --name spot -p 8080:8080 --link some-mongo:some-mongo -e MONGO_HOST='some-mongo' -e DISCORD_TOKEN='YOUR_TOKEN' biospheere/spoticord
```

## ⚖ [License](https://github.com/Biospheere/spoticord/blob/master/LICENSE)
MIT © [Niklas](https://github.com/Biospheere/)

 