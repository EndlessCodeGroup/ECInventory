// @ts-check

const lightCodeTheme = require('prism-react-renderer/themes/github');
const darkCodeTheme = require('prism-react-renderer/themes/dracula');
const defaultLocale = 'en';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'ECInventory',
  tagline: 'Change inventories how you need',
  url: 'https://endlesscodegroup.github.io',
  baseUrl: '/ECInventory/',
  trailingSlash: false,
  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',
  favicon: 'img/favicon.ico',
  organizationName: 'EndlessCodeGroup',
  projectName: 'ECInventory',

  i18n: {
    defaultLocale: defaultLocale,
    locales: ['en', 'ru'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: ({locale, versionDocsDirPath, docPath}) => {
            if (locale !== defaultLocale) {
              return `https://crowdin.com/project/ecinventory/${locale}`;
            }
            return `https://github.com/EndlessCodeGroup/ECInventory/tree/develop/website/${versionDocsDirPath}/${docPath}`;
          },
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'ECInventory',
        logo: {
          alt: 'ECInventory Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Documentation',
          },
          {
            type: 'localeDropdown',
            position: 'right',
          },
          {
            href: 'https://github.com/EndlessCodeGroup/ECInventory',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.gg/5NfPsgb',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Translations',
                href: 'https://crowdin.com/project/ecinventory',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/EndlessCodeGroup/ECInventory',
              },
            ],
          },
        ],
        copyright: `Copyright ?? ${new Date().getFullYear()} EndlessCode Group. Built with Docusaurus.`,
      },
      prism: {
        theme: lightCodeTheme,
        darkTheme: darkCodeTheme,
      },
    }),
};

module.exports = config;
