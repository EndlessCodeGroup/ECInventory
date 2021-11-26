CREATE TABLE IF NOT EXISTS ecinv_main
(
    id      INT NOT NULL PRIMARY KEY,
    version INT NOT NULL
);

REPLACE INTO ecinv_main
VALUES (42, 1);

CREATE TABLE IF NOT EXISTS ecinv_inventories
(
    id      CHAR(36)     NOT NULL PRIMARY KEY,
    layout  VARCHAR(255) NOT NULL,
    content TEXT         NOT NULL
);

CREATE TABLE IF NOT EXISTS ecinv_bindings
(
    holder_id    CHAR(36) NOT NULL,
    inventory_id CHAR(36) NOT NULL,
    PRIMARY KEY (holder_id, inventory_id),
    FOREIGN KEY (inventory_id)
        REFERENCES ecinv_inventories (id)
        ON UPDATE CASCADE ON DELETE CASCADE
);
