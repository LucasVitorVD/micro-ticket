CREATE TABLE show (
   id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
   name VARCHAR(100) NOT NULL,
   description TEXT NOT NULL,
   price NUMERIC(10,2) NOT NULL,
   total_tickets INT NOT NULL,
   available_tickets INT NOT NULL
);
