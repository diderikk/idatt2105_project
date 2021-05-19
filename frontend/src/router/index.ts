import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";
import { store } from "@/store";

const baseTitle = "Book That Room";

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
      title: "About",
    },
    component: () => import("../views/About.vue"),
  },
  {
    path: "/create-user",
    name: "CreateUser",
    meta: {
      permission: "Admin",
      title: "Create User",
    },
    component: () => import("../views/CreateUser.vue"),
  },
  {
    path: "/edit-user/:id",
    name: "EditUser",
    meta: {
      permission: "Admin",
      title: "Edit User",
    },
    component: () => import("../views/EditUser.vue"),
    props: true,
  },
  {
    path: "/create-reservation",
    name: "CreateReservation",
    meta: {
      permisson: "User",
      title: "Create Reservation",
    },
    component: () => import("../views/CreateReservation.vue"),
  },
  {
    path: "/edit-reservation/:id",
    name: "EditReservation",
    meta: {
      permisson: "User",
      title: "Create Reservation",
    },
    component: () => import("../views/EditReservation.vue"),
    props: true,
  },
  {
    path: "/create-room",
    name: "CreateRoom",
    meta: {
      permisson: "Admin",
      title: "Create Room",
    },
    component: () => import("../views/CreateRoom.vue"),
  },
  {
    path: "/edit-room/:code",
    name: "EditRoom",
    meta: {
      permisson: "Admin",
      title: "Edit Room",
    },
    component: () => import("../views/EditRoom.vue"),
    props: true,
  },
  {
    path: "/log-in",
    name: "LogIn",
    meta: {
      title: "Log in",
    },
    component: () => import("../views/LogIn.vue"),
  },
  {
    path: "/reservation-feed",
    name: "ReservationFeed",
    meta: {
      title: "Reservation feed",
      parmission: "User",
    },
    component: () => import("../views/ReservationFeed.vue"),
  },
  {
    path: "/users",
    name: "UserFeed",
    meta: {
      title: "Users",
      permission: "Admin",
    },
    component: () => import("../views/UserFeed.vue"),
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

router.afterEach((to, from, failure) => {
  document.title = (to.meta.title as string) || baseTitle;
});

export default router;
