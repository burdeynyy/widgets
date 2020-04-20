CREATE ALIAS IF NOT EXISTS H2GIS_SPATIAL FOR "org.h2gis.functions.factory.H2GISFunctions.load";
CALL H2GIS_SPATIAL();

DROP TABLE IF EXISTS widget;

CREATE SEQUENCE widget_seq
    START WITH 1
 -- for the test data ids to be matching db ids, in real system it's worth to consider setting bigger increment value
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

CREATE TABLE widget
(
    id                BIGINT            default widget_seq.nextval PRIMARY KEY,
    modification_date DATETIME NOT NULL DEFAULT now(),
    x                 INT      NOT NULL,
    y                 INT      NOT NULL,
    z                 INT      NOT NULL,
    width             INT      NOT NULL,
    height            INT      NOT NULL,
    rect              GEOMETRY as ST_MakeEnvelope(x + width / 2.0, y + height / 2.0, x - width / 2.0, y - height /2.0)

);

CREATE UNIQUE INDEX z_index ON widget (z);
CREATE SPATIAL INDEX widget_rectangle_index ON widget(rect);

insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 0, -6, 9, 4, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 1, 0, 9, 4, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 2, 7, 8, 4, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 3, 7, 6, 8, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 4, -3, 5, 2, 2);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 5, -6, 2, 2, 8);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 6, 3, 3, 4, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 7, 1, 0, 4, 6);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 8, 7, 0, 2, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 9, -1, -1, 4, 4);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 10, 1, -1, 2, 2);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 11, -2, -2, 2, 2);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 12, 6, -6, 6, 6);
insert into widget(id, modification_date, z, x, y, width, height)
values (default, default, 13, -5, -7, 4, 6);