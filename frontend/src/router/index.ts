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
      permission: "User",
      title: "Create Reservation",
    },
    component: () => import("../views/CreateReservation.vue"),
  },
  {
    path: "/edit-reservation/:id",
    name: "EditReservation",
    meta: {
      permission: "User",
      title: "Create Reservation",
    },
    component: () => import("../views/EditReservation.vue"),
    props: true,
  },
  {
    path: "/rooms",
    name: "RoomFeed",
    meta: {
      permission: "User",
      title: "Rooms",
    },
    component: () => import("../views/RoomFeed.vue"),
  },
  {
    path: "/create-room",
    name: "CreateRoom",
    meta: {
      permission: "Admin",
      title: "Create Room",
    },
    component: () => import("../views/CreateRoom.vue"),
  },
  {
    path: "/edit-room/:code",
    name: "EditRoom",
    meta: {
      permission: "Admin",
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
    path: "/reservations",
    name: "ReservationFeed",
    meta: {
      title: "Reservation Feed",
      permission: "User",
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
    path: "/users/:id",
    name: "UserProfile",
    meta: {
      title: "User Profile",
      permission: "User",
    },
    component: () => import("../views/UserProfile.vue"),
    props: true,
  },
  {
    path: "/chat/:roomCode",
    name: "Chat",
    meta: {
      title: "Room Chat",
      permission: "User",
    },
    component: () => import("../views/Chat.vue"),
    props: true,
  },
  {
    path: "/statistics",
    name: "Statistics",
    meta: {
      title: "Statistics",
      permission: "Admin",
    },
    component: () => import("../views/Stats.vue"),
  },
  {
    path: "/:catchAll(.*)",
    name: "NotFound",
    component: () => import("../views/PageNotFound.vue"),
  },
];

//Creates a router running i history mode, and containing the routes defined in the routes object
const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

/**
 * Handles authorization in frontend
 * Admins have permission to every page
 * Users have permission to page marked with permission User, or not marked with permission
 * Users trying to access admin pages will be sendt to the reservation feed
 * Not logged in users only have permission to pages not marked with permission
 * Not logged in users trying to access Admin or User pages will be sendt to log in page
 */
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.permission == "Admin")) {
    if (store.getters.getUser.isAdmin) {
      next();
      return;
    }
    if (store.getters.isUserLoggedIn) {
      //In case the user is logged in we dont want to kick the user out
      router.replace("/reservations");
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
    return;
  }
  next();
});

/**
 * After each route the title of the tab is changed to the route's title inside the meta tag, or the base title if the route does not define a title
 */
router.afterEach((to, from, failure) => {
  document.title = (to.meta.title as string) || baseTitle;
});

export default router;
