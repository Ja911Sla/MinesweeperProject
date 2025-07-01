# Use the Scala SBT image as base
FROM hseeberger/scala-sbt:8u222_1.3.5_2.13.1

# Update repositories to use a newer Debian version (bullseye)
RUN sed -i 's/stretch/bullseye/g' /etc/apt/sources.list

# Install required libraries and sbt
RUN apt-get update && \
    apt-get install -y sbt libxrender1 libxtst6 libxi6

# Set the working directory inside the container
WORKDIR /minesweeper

# Copy the contents of the current directory into the container's working directory
ADD . /minesweeper

# Run sbt to start the application
CMD sbt run