### Akka + mongo simple restaurants api

#### Build/run

1) Run unit tests
```
./xsbt.sh test
```

2) Run integration tests
```
./xsbt.sh it:test
```

3) Build docker container
```
./xsbt.sh docker
```
4) Run docker with mongo db
```
docker-compose up
```

Stop containers with 
```
docker-compose down
```

### Api 

GET /restaurants/<id>
Responses:
- 200. Gets a restaurant by id
- 404. The restaurant is not found
Sample response:
```
{
    "id": "aa905e21-1c6b-4508-84c3-eaa380847ccb",
	"name": "some_restaurant1",
	"cuisines": ["ukrainian"],
	"address": "address1",
	"description": "cool restaurant"
}
```

GET /restaurants
- 200. Gets the list of all stored restaurants.

POST /restaurants
Example body:
```
{
	"name" :"some_restaurant1",
	"cuisines" : ["ukrainian"],
	"address":"address1",
	"description": "cool restaurant"
}
```
- 201. Creates a restaurant. Returns the created restaurant.
- 409. The same restaurant by name and address is found. Responds with existing restaurant data.

PUT /restaurants/<id>
- 204. Updates a restaurant. No response body.

DELETE /restaurants/<id>
- 204. Deletes a restaurant. No response body.

