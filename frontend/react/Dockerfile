FROM node:19-alpine
ARG api_base_url
WORKDIR /app
COPY package*.json .
# this command for installing the dependencies
RUN npm i --silent
#copy the entire code of react inside /app
COPY . .
# tell our frontend where to look for out API , we have the option of building the image with different arguments
RUN echo "VITE_API_BASE_URL=${api_base_url}" > .env
EXPOSE 5173
#we use this command to run the container
CMD ["npm", "run", "dev"]

# What we are doing here in this file is basically packaging up the folder react (everything within it) into
# a container based of the node image version 19 alpine then we are going to copy all of our code ignoring
# the dockerfile as well as node_modules and .env into the /app Directory
# So with this file we can create a Docker Image and then from that image run the container.