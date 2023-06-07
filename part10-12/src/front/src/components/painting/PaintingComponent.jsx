import React, { Component, useEffect, useState } from "react";
import BackendService from "../../services/BackendService";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faSave } from "@fortawesome/free-solid-svg-icons";
import { alertActions } from "../../utils/Rdx";
import { connect } from "react-redux";
import { Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

const PaintingComponent = (props) => {
  const params = useParams();
  const [id, setId] = useState(params.id);
  const [name, setName] = useState("");
  const [artist, setArtist] = useState("");
  const [museum, setMuseum] = useState("");
  const [hidden, setHidden] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (parseInt(id) !== -1) {
      BackendService.retrievePainting(id)
        .then((resp) => {
          setName(resp.data.name);
        })
        .catch(() => setHidden(true));
    }
  }, []); // [] needed to call useEffect only once during component initialization
  //  this is necessary so that the value from the database is not written to the name state each time

  const onSubmit = (event) => {
    event.preventDefault();
    event.stopPropagation();
    let err = null;
    if (!name) err = "Название страны должно быть указано";
    if (err) props.dispatch(alertActions.error(err));
    let painting = { id, name, artist, museum };

    if (parseInt(painting.id) === -1) {
      BackendService.createPainting(painting)
        .then(() => navigate(`/paintings`))
        .catch(() => {});
    } else {
      BackendService.updatePainting(painting)
        .then(() => navigate(`/paintings`))
        .catch(() => {});
    }
  };

  if (hidden) return null;
  return (
    <div className="m-4">
      <div className=" row my-2 mr-0">
        <h3>Страна</h3>
        <div className="d-flex justify-content-end">
          <button
            className="btn btn-outline-secondary ml-auto btn-back"
            onClick={() => navigate(`/paintings`)}
          >
            <FontAwesomeIcon icon={faChevronLeft} /> Назад
          </button>
        </div>
      </div>
      <Form onSubmit={onSubmit}>
        <Form.Group>
          <Form.Label>Название</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите название картины"
            onChange={(e) => {
              setName(e.target.value);
            }}
            value={name}
            name="name"
            autoComplete="off"
          />
          <Form.Label>Автора</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите имя автора картины"
            onChange={(e) => {
              setArtist(e.target.value);
            }}
            value={artist}
            name="name"
            autoComplete="off"
          />
          <Form.Label>Музей</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите музей картины"
            onChange={(e) => {
              setMuseum(e.target.value);
            }}
            value={museum}
            name="name"
            autoComplete="off"
          />
        </Form.Group>
        <button className="btn btn-outline-secondary mt-3" type="submit">
          <FontAwesomeIcon icon={faSave} /> Сохранить
        </button>
      </Form>
    </div>
  );
};

export default connect()(PaintingComponent);
