---
--- Created by seul.
--- DateTime: 2023-04-21 오후 2:52
---
redis.call('mset',KEYS[1],ARGV[1],KEYS[2],ARGV[2]);
redis.call('expire',KEYS[1],ARGV[3]);
redis.call('expire',KEYS[2],ARGV[3]);