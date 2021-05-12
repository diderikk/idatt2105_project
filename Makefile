backend: backend_stop backend_build backend_run

backend_stop:
	-docker stop backend

backend_build:
	mvn -B -f ./backend package
	@docker build -t backend-server ./backend

backend_run:
	@docker run -d -p 8080:8080 --name backend --rm backend-server