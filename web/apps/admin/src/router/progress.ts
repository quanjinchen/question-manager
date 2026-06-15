import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

// 统一配置路由切换时使用的顶部进度条效果。
NProgress.configure({
  minimum: 0.1,
  speed: 400,
  showSpinner: false,
  trickle: true
});

// 开启路由切换进度条。
export function startProgress() {
  NProgress.start();
}

// 结束路由切换进度条。
export function stopProgress() {
  NProgress.done();
}
