# ForgeFIX/J

A low-latency FIX order gateway in Java 21. **Work in progress.**

ForgeFIX/J accepts FIX orders, normalizes them, routes them over a shared-memory message bus to an in-memory matching engine, persists every event, and publishes execution reports back over FIX. A market-data simulator drives synthetic order flow, and latency is measured end to end.

## Planned architecture

```
FIX client
    │
    ▼
 gateway            (QuickFIX/J)        FIX session handling, ingress/egress
    │
    ▼
   bus              (Aeron / Agrona)    inter-component messaging
    │
    ▼
matching engine     (price-time)        order matching
    │
    ▼
persistence         (Chronicle Queue)   event journaling, state recovery
    │
    ▼
execution reports  ──▶  back to FIX client
```

| Module | Responsibility | Stack |
|---|---|---|
| `forgefix-gateway` | FIX session handling, order ingress/egress | QuickFIX/J |
| `forgefix-bus` | Inter-component messaging | Aeron, Agrona |
| `forgefix-common` | Internal order/trade domain model | SBE-encoded codecs |
| `forgefix-engine` | Price-time-priority matching | — |
| `forgefix-persistence` | Event journaling and state recovery | Chronicle Queue |
| `forgefix-marketdata` | Synthetic order-flow simulator | — |
| `forgefix-bench` | Latency benchmarks (p50/p99) | JMH |

## Goals

- Correct FIX session lifecycle: logon, sequence-number discipline, scheduled reset, recovery on restart.
- Zero-allocation hot path where it matters; explicit about GC behaviour under load.
- Measurable latency, not claimed latency: JMH benchmarks checked into the repo.

## Status

Early development. Modules and benchmarks land incrementally; this README reflects the target design, not yet the current state. Build and run instructions will follow once the gateway-to-engine path is live.

## License

Apache-2.0.
