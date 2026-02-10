git remote add upstream https://github.com/yangzongzhuan/RuoYi-Vue3
git remote -v 

origin → 你的 fork 倉庫

upstream → 官方 RuoYi-Vue 倉庫

4. 開始二次開發

在 origin（你的倉庫）裡創建新分支，做功能開發： 盡量不要修改原來的任何代碼，避免衝突

5. 同步官方更新
   git fetch upstream
   git checkout master
   git merge upstream/master


git remote add upstream https://gitee.com/y_project/RuoYi-Vue.git
git fetch upstream
git checkout master
git merge upstream/master
git push origin master  # 可选