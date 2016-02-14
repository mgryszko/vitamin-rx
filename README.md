## TODO

Time slice events:

* was paused - one-time
* was stopped - one-time

Timed break events:

* is started - one-time
* in progress - periodic, every n minutes
* has elapsed - one-time

Actions:

* play sound
* speak
* display (notify via Growl)
* dim screen (only for has elapsed)

Code:

* usage of some magic number/values to indicate nothing

## DONE

Time slice events:

* started - one-time
* in progress - periodic, every n minutes
* will elapse soon - repeated, warn me: 10, 5, 3, 1 minutes before time elapses
* has elapsed - one-time
