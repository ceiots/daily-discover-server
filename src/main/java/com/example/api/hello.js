// api/hello.js
module.exports = async (req, res) => {
    const response = await fetch('https://springboot-633z.vercel.app/');
    const data = await response.text();
    res.status(200).json({ message: data });
};