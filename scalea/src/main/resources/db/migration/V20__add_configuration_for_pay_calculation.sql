INSERT INTO configuration (name, value, description) VALUES ('tap_low', '0', 'The minimal threshold of the gross salary for taxes');
INSERT INTO configuration (name, value, description) VALUES ('tap_low_perc', '0.0', 'The minimal threshold percentage of the gross salary for taxes');

INSERT INTO configuration (name, value, description) VALUES ('tap_med', '30000', 'The medium threshold of the gross salary for taxes');
INSERT INTO configuration (name, value, description) VALUES ('tap_med_perc', '0.13', 'The medium threshold percentage of the gross salary for taxes');

INSERT INTO configuration (name, value, description) VALUES ('tap_max', '150000', 'The maximum threshold of the gross salary for taxes');
INSERT INTO configuration (name, value, description) VALUES ('tap_max_perc', '0.23', 'The maximum threshold percentage of the gross salary for taxes');

INSERT INTO configuration (name, value, description) VALUES ('minimal_pay', '26000', 'The minimal gross salary for the calculation of the social and health insurance');
INSERT INTO configuration (name, value, description) VALUES ('maximum_pay', '114670', 'The maximum gross salary for the calculation of the social insurance');

INSERT INTO configuration (name, value, description) VALUES ('health_insurance_employer_perc', '0.017', 'The health insurance percentage for the employer');
INSERT INTO configuration (name, value, description) VALUES ('health_insurance_employee_perc', '0.017', 'The health insurance percentage for the employee');

INSERT INTO configuration (name, value, description) VALUES ('social_insurance_employer_perc', '0.15', 'The social insurance percentage for the employer');
INSERT INTO configuration (name, value, description) VALUES ('social_insurance_employee_perc', '0.095', 'The social insurance percentage for the employee');
