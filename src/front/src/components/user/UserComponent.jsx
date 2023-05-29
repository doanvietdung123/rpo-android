import React, { Component, useEffect, useState } from "react";
import BackendService from "../../services/BackendService";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronLeft, faSave } from "@fortawesome/free-solid-svg-icons";
import { alertActions } from "../../utils/Rdx";
import { connect } from "react-redux";
import { Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";

const UserComponent = (props) => {
  const params = useParams();
  const [id, setId] = useState(params.id);
  const [login, setLogin] = useState("");
  const [email, setEmail] = useState("");
  const [hidden, setHidden] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (parseInt(id) !== -1) {
      BackendService.retrieveUser(id)
        .then((resp) => {
          setLogin(resp.data.login);
        })
        .catch(() => setHidden(true));
    }
  }, []); // [] needed to call useEffect only once during component initialization
  //  this is necessary so that the value from the database is not written to the name state each time

  const onSubmit = (event) => {
    event.preventDefault();
    event.stopPropagation();
    let err = null;
    if (!login) err = "Название пользователи должно быть указано";
    if (err) props.dispatch(alertActions.error(err));
    let user = { id, login };

    if (parseInt(user.id) === -1) {
      BackendService.createUser(user)
        .then(() => navigate(`/users`))
        .catch(() => {});
    } else {
      BackendService.updateUser(user)
        .then(() => navigate(`/users`))
        .catch(() => {});
    }
  };

  if (hidden) return null;
  return (
    <div className="m-4">
      <div className=" row my-2 mr-0">
        <h3>Пользователи</h3>
        <div className="d-flex justify-content-end">
          <button
            className="btn btn-outline-secondary ml-auto btn-back"
            onClick={() => navigate(`/users`)}
          >
            <FontAwesomeIcon icon={faChevronLeft} /> Назад
          </button>
        </div>
      </div>
      <Form onSubmit={onSubmit}>
        <Form.Group>
          <Form.Label>Логин</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите логин пользователя"
            onChange={(e) => {
              setLogin(e.target.value);
            }}
            value={login}
            name="login"
            autoComplete="off"
          />
          <Form.Label>Логин</Form.Label>
          <Form.Control
            type="text"
            placeholder="Введите почту пользователя"
            onChange={(e) => {
              setEmail(e.target.value);
            }}
            value={email}
            name="email"
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

export default connect()(UserComponent);
