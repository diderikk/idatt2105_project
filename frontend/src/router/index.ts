import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/About.vue"),
  },
  {
    path: "/create-user",
    name: "CreateUser",
    component: () => import("../views/CreateUser.vue"),
  },
  {
    path: "/edit-user",
    name: "EditUser",
    component: () => import("../views/EditUser.vue"),
  },
  {
    path: "/create-reservation",
    name: "CreateReservation",
    component: () => import("../views/CreateReservation.vue"),
  },
  {
    path: "/edit-reservation",
    name: "EditReservation",
    component: () => import("../views/EditReservation.vue"),
  },
  {
    path: "/create-room",
    name: "CreateRoom",
    component: () => import("../views/CreateRoom.vue"),
  },
  {
    path: "/log-in",
    name: "LogIn",
    component: () => import("../views/LogIn.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
