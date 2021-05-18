import { useStore } from "@/store";
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import { store } from "@/store";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "Default",
    redirect: { path: "home" },
    component: Home,
    children: [
      {
        path: "home",
        name: "Home",
        component: Home,
      },
    ],
  },
  {
    path: "/about",
    name: "About",
    meta: {
      permission: "Admin",
    },
    component: () => import("../views/About.vue"),
  },
  {
    path: "/create-user",
    name: "CreateUser",
    component: () => import("../views/CreateUser.vue"),
  },
  {
    path: "/edit-user/:id",
    name: "EditUser",
    component: () => import("../views/EditUser.vue"),
    props: true,
  },
  {
    path: "/create-reservation",
    name: "CreateReservation",
    component: () => import("../views/CreateReservation.vue"),
  },
  {
    path: "/edit-reservation/:id",
    name: "EditReservation",
    component: () => import("../views/EditReservation.vue"),
    props: true,
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
  {
    path: "/:catchAll(.*)",
    name: "NotFound",
    component: () => import("../views/PageNotFound.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.permission == "Admin")) {
    if (store.getters.getUser.isAdmin) {
      next();
      return;
    }
    if (store.getters.isUserLoggedIn) {
      //In case the user is logged in we dont want to kick the user out
      router.getRoutes().length === 0
        ? router.replace("/log-in")
        : router.back();
      return;
    }
    console.log("hd");
    router.replace("/log-in");
    return;
  }
  if (to.matched.some((record) => record.meta.permission == "User")) {
    if (store.getters.isUserLoggedIn) {
      next();
      return;
    }
    router.replace("/log-in");
  }
  next();
});

export default router;
