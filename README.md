## TODO

### Functional

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
* dim screen (only for has elapsed)

### Technical

* Use minutes as a basic time slice unit
* Generate STARTING event on 0 time
* Usage of some magic number/values to indicate not configured
* Draw nice icon in Growl notification

## DONE

Time slice events:

* started - one-time
* in progress - periodic, every n minutes
* will elapse soon - repeated, warn me: 10, 5, 3, 1 minutes before time elapses
* has elapsed - one-time

Actions:

* display GUI notification
