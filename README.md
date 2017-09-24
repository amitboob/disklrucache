# Disk Lru Cache

This is a disked backed lru cache which makes it non volatile in nature.

While designing this I have considered following things :

  1. The Lru property is maintined on basis of entry counts.
  2. The disk space is not limited and so I have not taken total disk space into consideration.
  3. Right now if any new entry is made then the least recently used entry is removed, so only one entry is removed.
  4. If disk space has to be considered then the removal of entires from the cache can be on basis of the byets size taken by new entry i.e. entries would be deleted from cache until the total space freed by them is less than the total space required by the new entry. Right now this is not implemented as disk space is considered to be unlimited.
  5. Records file on disk maintains the access order of entries.
  6. There was a choice between availability and performace. For cache performance matters, so write to record file is made async. So a case may arise where memory cache had an entry while it fails to write to records so while rebuilding cache from disk that entry may be missed.
  7. The collisions are minimum i.e. for every key a different hashcode is generated.
  8. It cache does not require to return the key object.
  
