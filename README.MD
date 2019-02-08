###LX-watcher
App for watching Patient Portal for new available visits.  
Uses Selenium with Chrome Web Driver.
```
Usage: java -jar lx-watcher.jar [-hV] [--sound-disabled]
                                [--chrome-driver-version=<chromeDriverVersion>]
                                [--date-from=<dateFrom>] [--date-to=<dateTo>]
                                [--notify-run-channel=<notifyRunChannel>]
                                [--search-interval-ms=<searchIntervalMs>]
                                [--search-tires=<searchTries>] -l=<login>
                                -p=<password> -t=<visitType>
      --chrome-driver-version=<chromeDriverVersion>
                            Chrome Web Driver version to be downloaded when
                              executable is not present
                              Default: 2.46
      --date-from=<dateFrom>
                            Format: YYYY-MM-DD
      --date-to=<dateTo>    Format: YYYY-MM-DD
      --notify-run-channel=<notifyRunChannel>
                            Notify.run channel name. Enables push message notifier
                              when set.
                            See https://notify.run/ for details.
      --search-interval-ms=<searchIntervalMs>
                              Default: 60000
      --search-tires=<searchTries>
                              Default: 120
      --sound-disabled      Disables sound notifier
  -h, --help                prints this help and exits
  -l, -u, --login=<login>
  -p, --password=<password>
  -t, --visit-type=<visitType>
                            Valid values: PEDIATRICIAN_SICK, PEDIATRICIAN_HEALTHY,
                              INTERNIST
  -V, --version             prints version information and exits

```