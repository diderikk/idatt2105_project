import { useStore } from "@/store";
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Home from "../views/Home.vue";

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
    meta: {
      requiresLoggedIn: true,
    },
  },
  {
    path: "/about",
    name: "About",
    meta: {
      requiresAdmin: true,
    },
    component: () => import("../views/About.vue"),
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

const store = useStore();
router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAdmin)) {
    if (store.getters.users.isAdmin) {
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
    router.replace("log-in");
    return;
  }
  if (to.matched.some((record) => record.meta.requiresLoggedIn)) {
    if (store.getters.isUserLoggedIn) {
      next();
      return;
    }
    router.replace("log-in");
  }
  next();
});

export default router;
