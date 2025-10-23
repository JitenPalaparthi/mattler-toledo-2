.PHONY: build up down logs push demo
build:
	docker compose build --no-cache

up:
	docker compose up -d

down:
	docker compose down -v

logs:
	docker compose logs -f --tail=200

# Quick curl hits to generate some metrics
demo:
	curl -s -X POST localhost:8080/api/order -H 'Content-Type: application/json' -d '{"orderId":"1","item":"widget"}' | jq . ; \
		for i in $$(seq 1 25); do curl -s -X POST localhost:8080/api/order -H 'Content-Type: application/json' -d "{"orderId":"$$i","item":"gadget"}" >/dev/null; done
