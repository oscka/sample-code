---
--- get.lua
--- Created by seul.
--- DateTime: 2023-04-21 오후 2:54
---
return {
    redis.call('mget',KEYS[1],KEYS[2]), redis.call('ttl',KEYS[1])
}