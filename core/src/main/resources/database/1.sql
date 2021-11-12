CREATE TABLE IF NOT EXISTS ecinv_main
(
    id      INT NOT NULL,
    version INT NOT NULL,
    CONSTRAINT ecinv_main_pk PRIMARY KEY (id)
);

INSERT OR
REPLACE
INTO ecinv_main
VALUES (42, 1);

CREATE TABLE IF NOT EXISTS ecinv_inventories
(
    id      CHAR(36)     NOT NULL,
    layout  VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL,
    CONSTRAINT ecinv_inventories_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS ecinv_bindings
(
    holder_id    CHAR(36) NOT NULL,
    inventory_id CHAR(36) NOT NULL,
    CONSTRAINT ecinv_inventory_bindings_pk PRIMARY KEY (holder_id, inventory_id),
    CONSTRAINT ecinv_inventory_bindings_ecinv_inventories_id_fk FOREIGN KEY (inventory_id)
        REFERENCES ecinv_inventories
        ON UPDATE CASCADE ON DELETE CASCADE
);
